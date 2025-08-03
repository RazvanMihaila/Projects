FROM php:8.4-rc-apache
WORKDIR /var/www/html
RUN apt-get update -y && apt-get install -y libmariadb-dev libicu-dev
RUN docker-php-ext-install mysqli pdo pdo_mysql
RUN a2enmod rewrite