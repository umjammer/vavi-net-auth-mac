[![Release](https://jitpack.io/v/umjammer/vavi-speech-rpc.svg)](https://jitpack.io/#umjammer/vavi-speech-rpc)
[![Java CI](https://github.com/umjammer/vavi-speech-rpc/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-speech-rpc/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-speech-rpc/actions/workflows/codeql.yml/badge.svg)](https://github.com/umjammer/vavi-speech-rpc/actions/workflows/codeql.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-speech-rpc

RPC client/server for JSR-113 JSAPI2

### sample

 * make AquesTalk10 work on arm64 mac. run rcp server on Rosetta2, then call rpc speech synthesizer.

## Install

 * [maven](https://jitpack.io/#umjammer/vavi-speech-rpc)

## Usage

 [sample](src/test/java/vavi/speech/rpc/jsapi2/TestCase.java) 

## References

 * [jersey post server](https://stackoverflow.com/questions/29183274/jax-rs-jersey-rest-webservice-posting-a-array-generated-by-input-from-user)
 * [jersey post client](https://qiita.com/noobar/items/a96e07e441241b1e0215)
 * [jersey DI](https://qiita.com/atti/items/3f6f43c5168323344427)

## TODO

 * .app doesn't start by click on finder (calling stub directly from a terminal is succeeded)
 * server side, speech engine spi pluggable, specifiable