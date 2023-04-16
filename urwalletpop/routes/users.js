module.exports = function (app) {
  app.get('/signup', function (req, res) {
    res.render("signup.twig");
  });
}