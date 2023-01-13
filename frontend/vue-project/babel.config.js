/* eslint-disable no-var */
module.exports = (api) => {
  var isProd = api.cache.invalidate(() => process.env.BUILD_MODE === 'prod');
  var plugins = [];
  if (isProd) {
    plugins.push(['transform-remove-console', { exclude: ['error', 'warn', 'info'] }]);
  }
  return {
    presets: ['@vue/cli-plugin-babel/preset'],
    plugins,
  };
};
