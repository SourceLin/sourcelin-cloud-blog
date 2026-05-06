const fs = require('fs');
const path = require('path');

const indexScssPath = path.join(__dirname, 'src', 'assets', 'styles', 'index.scss');
console.log(fs.readFileSync(indexScssPath, 'utf8'));
