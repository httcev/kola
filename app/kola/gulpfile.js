var gulp = require('gulp');
var gutil = require('gulp-util');
var bower = require('bower');
var concat = require('gulp-concat');
var sass = require('gulp-sass');
var minifyCss = require('gulp-minify-css');
var rename = require('gulp-rename');
var sh = require('shelljs');
var preprocess = require('gulp-preprocess');

var paths = {
  sass: ['./scss/**/*.scss']
};

gulp.task('default', ['sass', 'set-env']);

gulp.task('sass', function(done) {
  gulp.src('./scss/ionic.app.scss')
    .pipe(sass({
      errLogToConsole: true
    }))
    .pipe(gulp.dest('./www/css/'))
    .pipe(minifyCss({
      keepSpecialComments: 0
    }))
    .pipe(rename({ extname: '.min.css' }))
    .pipe(gulp.dest('./www/css/'))
    .on('end', done);
});

gulp.task('watch', function() {
  gulp.watch(paths.sass, ['sass']);
});

gulp.task('install', ['git-check'], function() {
  return bower.commands.install()
    .on('log', function(data) {
      gutil.log('bower', gutil.colors.cyan(data.id), data.message);
    });
});

gulp.task('set-env', function() {
  // fallback to "development" if ENV variable is not defined
  if (!process.env.ENV) {
    process.env.ENV = "dev";
  }
  var appPackage = "de.httc.kola" + (process.env.ENV != "prod" ? ("." + process.env.ENV) : "");
  var appName = "KOLA" + (process.env.ENV != "prod" ? (" [" + process.env.ENV + "]") : "");
  var appVersion = "1.0.2";

  gulp.src('./conf/app.js')
    .pipe(preprocess({context: { APP_NAME: appName, APP_VERSION: appVersion }}))
    .pipe(gulp.dest('./www/js/'));

  return gulp.src('./conf/config.xml')
    .pipe(preprocess({context: { APP_PACKAGE: appPackage, APP_NAME: appName, APP_VERSION: appVersion }}))
    .pipe(gulp.dest('.'));
});

gulp.task('git-check', function(done) {
  if (!sh.which('git')) {
    console.log(
      '  ' + gutil.colors.red('Git is not installed.'),
      '\n  Git, the version control system, is required to download Ionic.',
      '\n  Download git here:', gutil.colors.cyan('http://git-scm.com/downloads') + '.',
      '\n  Once git is installed, run \'' + gutil.colors.cyan('gulp install') + '\' again.'
    );
    process.exit(1);
  }
  done();
});
