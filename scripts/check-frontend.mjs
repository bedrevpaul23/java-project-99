import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import queryString from 'query-string';

const html = readFileSync(resolve('src/main/resources/static/index.html'), 'utf8');
const assetName = html.match(/\/assets\/(index-[A-Za-z0-9_-]+\.js)/)?.[1];
if (!assetName) {
  throw new Error('Published frontend bundle is missing');
}
const bundle = readFileSync(resolve('src/main/resources/static/assets', assetName), 'utf8');
if (!/sessionStorage\.setItem\(["'`]token["'`]/.test(bundle)
    || !/localStorage\.setItem\(["'`]authSession["'`],\s*["'`]active["'`]\)/.test(bundle)
    || !/localStorage\.getItem\(["'`]authSession["'`]\)/.test(bundle)
    || !/localStorage\.removeItem\(["'`]authSession["'`]\)/.test(bundle)
    || !/localStorage\.removeItem\(["'`]user["'`]/.test(bundle)
    || /localStorage\.setItem\(["'`](?:token|user)["'`]/.test(bundle)
    || /localStorage\.getItem\(["'`](?:token|user)["'`]/.test(bundle)
    || /JSON\.stringify\(\{username:\w+,password:\w+\}\)/.test(bundle)) {
  throw new Error('Published frontend can persist credentials in localStorage');
}

if (queryString.parse('titleCont=Task%20A').titleCont !== 'Task A') {
  throw new Error('Published frontend query parser is incompatible with filtering');
}

const app = readFileSync(
  resolve('node_modules/@hexlet/java-task-manager-frontend/src/App.tsx'),
  'utf8',
);
const users = readFileSync(
  resolve('node_modules/@hexlet/java-task-manager-frontend/src/components/users.tsx'),
  'utf8',
);
const list = users.split('export const UserList = () => (')[1]?.split('export const UserShow = () => (')[0];
if (!app.includes('recordRepresentation="email"')
    || !list?.includes('<Datagrid rowClick="edit">')
    || !list.includes('source="email"')) {
  throw new Error('The authenticated user directory is incompatible with task assignment');
}
