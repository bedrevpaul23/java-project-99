import { execFileSync } from 'node:child_process';
import { copyFileSync, readFileSync, readdirSync, unlinkSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';

const root = resolve('node_modules/@hexlet/java-task-manager-frontend');
const staticRoot = resolve('src/main/resources/static');

function patchFile(relativePath, replacements) {
  const file = resolve(root, relativePath);
  let source = readFileSync(file, 'utf8');
  for (const [before, after] of replacements) {
    if (after.includes(before) && source.includes(after)) {
      continue;
    }
    if (source.includes(before)) {
      source = source.replaceAll(before, after);
    } else if (!source.includes(after)) {
      throw new Error(`Unexpected upstream frontend source: ${relativePath}: ${before}`);
    }
  }
  writeFileSync(file, source);
}

patchFile('src/authProvider.ts', [
  ['localStorage.setItem("token", response.data)', 'sessionStorage.setItem("token", response.data)'],
  ['JSON.stringify({ username, password })', 'JSON.stringify({ username })'],
  ['localStorage.setItem("user", userNameData)', 'sessionStorage.setItem("user", userNameData)'],
  ['localStorage.removeItem("token")', 'sessionStorage.removeItem("token")'],
  ['localStorage.getItem("token")', 'sessionStorage.getItem("token")'],
  ['localStorage.getItem("user")', 'sessionStorage.getItem("user")'],
  ['status === 401 || status === 403', 'status === 401'],
]);
patchFile('src/dataProvider.ts', [
  ['localStorage.getItem("token")', 'sessionStorage.getItem("token")'],
]);
patchFile('src/main.tsx', [
  ['import React from "react";', 'localStorage.removeItem("user");\nlocalStorage.removeItem("token");\nimport React from "react";'],
]);

execFileSync('npm', ['run', 'build', '--prefix', root], { stdio: 'inherit' });

const assets = readdirSync(resolve(root, 'dist/assets')).filter(name => /^index-[A-Za-z0-9_-]+\.js$/.test(name));
if (assets.length !== 1) {
  throw new Error('Expected one frontend JavaScript bundle');
}
const previousHtml = readFileSync(resolve(staticRoot, 'index.html'), 'utf8');
const oldAsset = previousHtml.match(/\/assets\/(index-[A-Za-z0-9_-]+\.js)/)?.[1];
if (!oldAsset) {
  throw new Error('Could not identify the previous published bundle');
}

copyFileSync(resolve(root, 'dist/assets', assets[0]), resolve(staticRoot, 'assets', assets[0]));
writeFileSync(resolve(staticRoot, 'index.html'), previousHtml.replace(oldAsset, assets[0]));
if (oldAsset !== assets[0]) {
  unlinkSync(resolve(staticRoot, 'assets', oldAsset));
}
