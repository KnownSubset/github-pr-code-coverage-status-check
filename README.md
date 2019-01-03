# Github Coverage reporter

[![Build Status](https://travis-ci.org/knownSubset/github-coverage-reporter.svg?branch=master)](https://travis-ci.org/knownSubset/github-coverage-reporter)

Jenkins plugin for reporting code coverage as a github status check.

## Screenshots

![Screenshot of success status](https://raw.githubusercontent.com/KnownSubset/github-coverage-reporter/readme/assets/coverage-success.png)

![Screenshot of failure status](https://raw.githubusercontent.com/KnownSubset/github-coverage-reporter/readme/assets/coverage-failure.png)

## License

All code is licensed under [Apache 2.0 License](LICENSE)

## Usage

coverageRateType: Line, Branch, Overall
coverageXmlType: jacoco, cobertura, sonarqube

```jenkins
def comparisonOperator = [$class: 'ComparisonOption', value: '75']
step([$class: 'GithubCoveragePublisher', filepath: 'coverage/cobertura-coverage.xml', coverageXmlType: 'cobertura', comparisonOption: comparisonOperator, coverageRateType: 'Overall'])
```

## FAQ

Does this work on Github/Github Enterprise?

