include ../sdk.mk

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.js \

DEP_CATEGORIES = \
org.argeo.cms \

all: npm-ci webpack osgi

webpack:
	npm run build-prod

webpack-dev:
	npm run build

clean:
	$(foreach bundle, $(BUNDLES), rm -rf $(bundle)/org) 

npm-ci:
	npm ci
	
npm-install:
	npm install

# Will update package-lock.json based on the constraints defined in package.json
# Note: run `npm outdated` before for an overview
npm-update:
	npm outdated
	npm update
	npm outdated

jsdoc:
	node_modules/.bin/jsdoc -r src \
		-d $(SDK_BUILD_BASE)/jsdoc/argeo-platform-js

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk
