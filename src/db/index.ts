import 'dotenv/config';
import { drizzle } from 'drizzle-orm/node-postgres';
import { Pool } from 'pg';
import * as schema from './schema.ts';

declare global {
  var _postgresPool: Pool | undefined;
}

export const getResolvedDatabaseUrl = (): string | undefined => {
  let url = process.env.DATABASE_URL;
  if (!url) return undefined;

  // When running outside Render's internal network (e.g., in AI Studio dev environment),
  // internal Render hostnames (dpg-xxxx-a) must be resolved to their external hostname.
  if (url.includes('@dpg-') && !url.includes('.render.com')) {
    url = url.replace(/@(dpg-[a-z0-9]+-[a-z0-9]+)(\/|\?|$)/, '@$1.oregon-postgres.render.com$2');
  }

  return url;
};

export const createPool = () => {
  if (!global._postgresPool) {
    const databaseUrl = getResolvedDatabaseUrl();
    const isRemote = Boolean(
      databaseUrl &&
        (databaseUrl.includes('.render.com') ||
          databaseUrl.includes('ssl=true') ||
          !databaseUrl.includes('localhost'))
    );

    global._postgresPool = databaseUrl
      ? new Pool({
          connectionString: databaseUrl,
          max: 10,
          connectionTimeoutMillis: 5000,
          ssl: isRemote ? { rejectUnauthorized: false } : undefined,
        })
      : new Pool({
          host: process.env.SQL_HOST || 'localhost',
          port: process.env.SQL_PORT
            ? parseInt(process.env.SQL_PORT, 10)
            : 5432,
          user: process.env.SQL_USER || 'postgres',
          password: process.env.SQL_PASSWORD || '1234',
          database: process.env.SQL_DB_NAME || 'metapro_db',
          max: 10,
          connectionTimeoutMillis: 2000,
        });

    global._postgresPool.on('error', (err) => {
      // Don't crash process on pool client error
      console.warn('PostgreSQL pool connection event:', err.message);
    });
  }

  return global._postgresPool;
};

const pool = createPool();

export const db = drizzle(pool, { schema });