module server.dictionary {
	requires java.base;
	requires transitive utils.languages;
	requires transitive utils.dictionary;
	exports uninsubria.server.dictionary.manager;
}