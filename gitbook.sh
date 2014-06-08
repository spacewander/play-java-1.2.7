#!/bin/sh

gitbook build ./play-tutorial --output='./play教程' || exit 1
cd ./play教程 
sed -i 's/<a href="https:\/\/github.com\/null.*"/<a href="#"/g' ./*.html
