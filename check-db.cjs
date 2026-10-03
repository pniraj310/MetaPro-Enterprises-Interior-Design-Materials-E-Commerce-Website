require('dotenv').config();
const { Client } = require('pg');

const client = new Client({
  connectionString: process.env.DATABASE_URL
});

client.connect()
  .then(() => client.query("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name"))
  .then(result => console.log(result.rows.map(row => row.table_name).join('\n')))
  .catch(error => console.error(error.message))
  .finally(() => client.end());
