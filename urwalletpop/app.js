var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var app = express();

let rest = require('request');
app.set('rest', rest);

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type,Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});

let jwt = require('jsonwebtoken');
app.set('jwt', jwt);

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

let crypto = require('crypto');
let fileUpload = require('express-fileupload');
app.use(fileUpload({
    limits: { fileSize: 50 * 1024 * 1024 },
    createParentPath: true
}));
app.set('uploadPath', __dirname);
app.set('clave', 'abcdefg');
app.set('crypto', crypto);
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

const {MongoClient} = require("mongodb");
const url = 'mongodb://localhost:27017';
app.set('connectionStrings', url);

const logsRepository = require("./repositories/logsRepository.js");
logsRepository.init(app, MongoClient);

const loggerMiddleware = require("./routes/loggerMiddleware");

app.use(logger('dev'));
app.use(loggerMiddleware(logsRepository));

const userSessionRouter = require('./routes/userSessionRouter');
app.use('/home', userSessionRouter);
app.use('/user/*', userSessionRouter);
app.use('/offer/*', userSessionRouter);
app.use('/conversation/*', userSessionRouter);
app.use('/users/searchList', userSessionRouter);
app.use('/users/logout', userSessionRouter);

const userAdminSessionRouter = require('./routes/userAdminSessionRouter');
app.use('/users/list', userAdminSessionRouter);
app.use('/log/list', userAdminSessionRouter);
app.use('/log/delete', userAdminSessionRouter);
app.use('/users/delete', userAdminSessionRouter);

const userStandardSessionRouter = require('./routes/userStandardSessionRouter');
app.use('/offer/add', userStandardSessionRouter);
app.use('/offer/ownedList', userStandardSessionRouter);
app.use('/offer/purchasedList', userStandardSessionRouter);
app.use('/conversation/list', userStandardSessionRouter);


const offerRepository = require("./repositories/offerRepository.js");
const usersRepository = require("./repositories/usersRepository.js");
const conversationRepository = require("./repositories/conversationRepository.js");
usersRepository.init(app, MongoClient);
offerRepository.init(app, MongoClient);
conversationRepository.init(app, MongoClient);

const userTokenRouter = require('./routes/userTokenRouter');
app.use("/api/offers/", userTokenRouter);

require("./routes/logs.js")(app, logsRepository);
require("./routes/users.js")(app, usersRepository, offerRepository,logsRepository);
require("./routes/offers.js")(app, offerRepository, usersRepository);
require("./routes/api/usersAPI.js")(app,usersRepository,offerRepository,conversationRepository);


let indexRouter = require('./routes/index');
app.use('/', indexRouter);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

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

    errors = [{field: "Error no controlado", message: err.message}];
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
