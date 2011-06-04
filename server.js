var express = require('express')
  , conjugator = require('./html/korean/conjugator');

var app = express.createServer();

app.configure(function(){
  app.use(express.methodOverride());
  app.use(express.bodyParser());
  app.use(app.router);
  app.set('view engine', 'jade');
});

app.configure('development', function(){
  app.use(express.static(__dirname + '/html'));
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function(){
  app.use(express.static(__dirname + '/html'));
  app.use(express.errorHandler());
});

app.get('/', function (req, res) {
  var infinitive = conjugator.base(req.query.infinitive || '하') + '다';
  conjugator.conjugate(infinitive, req.query.regular, function(conjugations) {
    res.render('index.jade', {
      infinitive: infinitive,
      conjugations: conjugations
    });
  });
});

app.listen(3000);
console.log('Server running at http://127.0.0.1:3000/');
