var express = require('express')
  , conjugator = require('./html/korean/conjugator')
  , hangeul = require('./html/korean/hangeul')
  , stemmer = require('./html/korean/stemmer')
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

app.get('/stem', function (req, res) {
  var search = req.query.search;
  stemmer.stem_lookup(req.query.search, select_by_stem, function(results) {
    res.render('stem-search.jade', {
      search: search,
      results: results
    });
  });
});

app.get('/', function (req, res) {
  var search = req.query.search;
  // redirect old requests from when the site only handled infinitive lookups
  if (req.query.infinitive) {
    return res.redirect('/?search=' + req.query.infinitive);
  }
  if (!search) {
    return res.redirect('/?search=하다');
  }
  // if non-hangeul characters appear in the input search the definitions
  if (!hangeul.is_hangeul_string(search)) {
    select_by_definition.all(
      search + '*',
      function(err, results) {
        // redirect to the conjugation if there is only one result
        if (results.length == 1) {
          return res.redirect('/?search=' + results[0].infinitive);
        }
        res.render('definition-search.jade', {
          search: search,
          results: results
        });
      }
    );
  } else {
    var infinitive = conjugator.base(search || '하') + '다';
    var verbs = [];
    var definitions = [];
    var valid_verbs = [];
    select_definition.all(infinitive.replace(/ /g, ''), function(err, results) {
      definitions = results.map(function(x) { return x.definition });
      select_verb_type.all(infinitive.replace(/ /g, ''), function(err, results) {
        valid_verbs = results;
        conjugator.conjugate(
          infinitive,
          req.query.regular,
          function(conjugations) {
            res.render('index.jade', {
              search: infinitive,
              conjugations: conjugations,
              definitions: definitions,
              valid_verbs: valid_verbs
            });
          }
        );
      });
    });
  }
});

var select_definition
  , select_verb_type
  , select_by_definition
  , select_by_stem;

var db = new sqlite3.Database('korean-verb-database/korean-verbs.sqlite', sqlite3.OPEN_READONLY, function() {
  select_definition = db.prepare("SELECT definition FROM verbs WHERE infinitive = ?");
  select_verb_type = db.prepare("SELECT infinitive, verb_type FROM valid_verbs WHERE infinitive = ?");
  select_by_definition = db.prepare("SELECT infinitive, definition FROM verbs WHERE definition MATCH ?");
  select_by_stem = db.prepare("SELECT infinitive FROM stems WHERE stem = ?");
  app.listen(3000);
  console.log('Server running at http://127.0.0.1:3000/');
});
