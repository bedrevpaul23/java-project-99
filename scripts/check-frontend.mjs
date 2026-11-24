import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

const html = readFileSync(resolve('src/main/resources/static/index.html'), 'utf8');
const assetName = html.match(/\/assets\/(index-[A-Za-z0-9_-]+\.js)/)?.[1];
if (!assetName) {
  throw new Error('Published frontend bundle is missing');
}
const bundle = readFileSync(resolve('src/main/resources/static/assets', assetName), 'utf8');
if (!/sessionStorage\.setItem\(["'`]token["'`]/.test(bundle)
    || !/localStorage\.removeItem\(["'`]user["'`]/.test(bundle)
    || /localStorage\.setItem\(["'`](?:token|user)["'`]/.test(bundle)
    || /localStorage\.getItem\(["'`](?:token|user)["'`]/.test(bundle)
    || /JSON\.stringify\(\{username:\w+,password:\w+\}\)/.test(bundle)) {
  throw new Error('Published frontend can persist credentials in localStorage');
}

const users = readFileSync(resolve('node_modules/@hexlet/java-task-manager-frontend/src/components/users.tsx'), 'utf8');
const list = users.split('export const UserList = () => (')[1]?.split('export const UserShow = () => (')[0];
const show = users.split('export const UserShow = () => (')[1];
if (!list?.includes('record.email ? "edit" : false')
    || !list.includes('source="displayName"')
    || !show?.includes('source="email"')
    || !show.includes('source="firstName"')
    || !show.includes('source="lastName"')) {
  throw new Error('The user directory or owner profile has regressed');
}
