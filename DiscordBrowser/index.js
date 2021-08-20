const {app, session, BrowserWindow} = require('electron');
const express = require('express');
const express_app = express();

var token = null;

const filter = {
  urls: ['*://discord.com/*/users/@me']
}

function createWindow () {
  session.defaultSession.webRequest.onBeforeSendHeaders(filter, (details, callback) => {
    token = details.requestHeaders['Authorization'];
    callback({ requestHeaders: details.requestHeaders });
  });

  const win = new BrowserWindow({
    width: 800,
    height: 600
  })

  win.loadURL('https://discord.com/login');
}

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') {
    app.quit();
  }
})

express_app.get('/', (req, res) => {
  res.send(token);
});

express_app.get('/stop', (req, res) => {
  app.quit();
});

express_app.listen(2525, () => {});

app.whenReady().then(() => {
  createWindow()
});
