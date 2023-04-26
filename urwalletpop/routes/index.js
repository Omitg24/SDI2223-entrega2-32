var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function (req, res, next) {
    res.render('index', {
        user: req.session.user,
        role: req.session.role,
        amount: req.session.amount,
        date: req.session.date
    });
});

module.exports = router;
