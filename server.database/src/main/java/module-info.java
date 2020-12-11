module server.database {
    exports uninsubria.server.db.api;
    exports uninsubria.server.db.dao to server.dbpopulator;
    exports uninsubria.server.db.businesslayer to server.dbpopulator;

    requires transitive java.sql;
	requires utils.serviceResults;
	requires utils.business;
	requires utils.languages;
    requires server.dice;
    requires server.email;
    requires java.mail;
    requires utils.security;
}