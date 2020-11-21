module server.dbpopulator {
    exports uninsubria.server.dbpopulator;

    requires server.database;
    requires utils.business;
    requires utils.security;
    requires utils.languages;
    requires server.dice;
    requires server.dictionary;
    requires org.jgrapht.core;
}