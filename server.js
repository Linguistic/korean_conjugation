var express = require('express')
  , conjugator = require('./html/korean/conjugator')
  , sqlite3 = require('sqlite3');

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
  var definitions = [];
  show_conjugations = function() {
    conjugator.conjugate(infinitive, req.query.regular, function(conjugations) {
      res.render('index.jade', {
        infinitive: infinitive,
        conjugations: conjugations,
        definitions: definitions
      });
    });
  };
  if (db.open) {
    select_definition.all(infinitive, function(err, results) {
      definitions = results.map(function(x) { return x.definition });
      show_conjugations();
    });
  } else {
    show_conjugations();
  }
});

var select_definition;

var db = new sqlite3.Database('korean-verb-database/korean-verb-definitions.sqlite', sqlite3.OPEN_READONLY, function() {
  select_definition = db.prepare("SELECT definition FROM verbs WHERE infinitive = ?");
  app.listen(3000);
  console.log('Server running at http://127.0.0.1:3000/');
});
