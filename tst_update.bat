call npm run --prefix frontend\vue-project build

docker cp frontend\vue-project\dist\. ashev-demo-fronthost-1:/var/www/localhost