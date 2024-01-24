.PHONY: install-clojure
install-clojure:
	brew bundle

.PHONY: generate-docs
generate-docs:
	lein codox

.PHONY: tests
tests:
	lein test
