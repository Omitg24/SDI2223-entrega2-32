var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var app = express();
let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

let crypto = require('crypto');

app.set('uploadPath', __dirname);
app.set('clave', 'abcdefg');
app.set('crypto', crypto);
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

const {MongoClient} = require("mongodb");
const url = 'mongodb://localhost:27017';
app.set('connectionStrings', url);

const userSessionRouter = require('./routes/userSessionRouter');
app.use('/home', userSessionRouter);
app.use('/user/*', userSessionRouter);
app.use('/offer/*', userSessionRouter);
app.use('/conversation/*', userSessionRouter);
app.use('/log', userSessionRouter);
app.use('/users/logout', userSessionRouter);

const userAdminSessionRouter = require('./routes/userAdminSessionRouter');
app.use('/users/list', userAdminSessionRouter);
app.use('/users/delete', userAdminSessionRouter);

const usersRepository = require("./repositories/usersRepository.js");
usersRepository.init(app, MongoClient);
require("./routes/users.js")(app, usersRepository);

const logsRepository = require("./repositories/logsRepository.js");
logsRepository.init(app, MongoClient);
require("./routes/logs.js")(app, logsRepository);

const loggerMiddleware = require("./routes/loggerMiddleware.js")(logsRepository);

let indexRouter = require('./routes/index');
app.use('/', indexRouter);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(loggerMiddleware);

app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// catch 404 and forward to error handler
app.use(function (req, res, next) {
    next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    errors = [{field: "Error general", message: err.message}];
    // render the error page
    res.status(err.status || 500);
    res.render('error', {
        errors: errors,
        user: req.session.user,
        role: req.session.role,
        amount: req.session.amount,
        date: req.session.date
    });
});

module.exports = app;
