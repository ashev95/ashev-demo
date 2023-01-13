const { defineConfig } = require('@vue/cli-service')
const fs = require('fs')
module.exports = defineConfig({
  transpileDependencies: true,
  pluginOptions: {
    i18n: {
      locale: 'ru-RU',
      fallbackLocale: 'en-US',
      localeDir: 'locales',
      enableLegacy: false,
      runtimeOnly: false,
      compositionOnly: false,
      fullInstall: true
    }
  },
  configureWebpack: {
    devServer: {
      proxy: {
        '/api': {
          target: 'https://localhost:8091',
          changeOrigin: true,
          secure: false, //false - dev only, true - prod only
          pathRewrite: { '^/api': '' }
        }
      },
      client: {
        webSocketURL: 'ws://localhost:8080/ws',
      },
      server: {
        type: 'https',
        options: {
          pfx: fs.readFileSync('./../../cert/tst/localhost.pfx'),
          passphrase: '100595'
        }
      }
    }
  },
  outputDir: process.env.BUILD_MODE === 'prod'
      ? 'dist/prod'
      : 'dist/tst'
})
