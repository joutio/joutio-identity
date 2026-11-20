## [2.4.11](https://github.com/joutio/joutio-identity/compare/v2.4.10...v2.4.11) (2026-11-20)

### Performance Improvements

* **docker:** JDK 25 AOT cache at startup ([bc1fe86](https://github.com/joutio/joutio-identity/commit/bc1fe865895f54367b116ee03e299182446bd46f))

## [2.4.10](https://github.com/joutio/joutio-identity/compare/v2.4.9...v2.4.10) (2026-11-20)

### Bug Fixes

* **docker:** rebuild on patched base images ([51b3557](https://github.com/joutio/joutio-identity/commit/51b3557b41b95230b7075e4c0b7c7242a377442c))

## [2.4.9](https://github.com/joutio/joutio-identity/compare/v2.4.8...v2.4.9) (2026-11-20)

### Bug Fixes

* **deps:** joutio-parent 2.4.0, instance-scoped groups on one Kafka consumer per pod ([1901c1c](https://github.com/joutio/joutio-identity/commit/1901c1cc07acb1227131044f1e920210710de330))

## [2.4.8](https://github.com/joutio/joutio-identity/compare/v2.4.7...v2.4.8) (2026-11-20)

### Bug Fixes

* **deps:** joutio-parent 2.3.0, readiness waits for instance-scoped processors ([9378ee4](https://github.com/joutio/joutio-identity/commit/9378ee439724d86333681d3616bbf330d299df76))

## [2.4.7](https://github.com/joutio/joutio-identity/compare/v2.4.6...v2.4.7) (2026-11-20)

### Bug Fixes

* **deps:** joutio-parent 2.2.1, distinct Kafka consumer metrics ([1cbc79a](https://github.com/joutio/joutio-identity/commit/1cbc79a6409cb59c61d5d837e283c300eefa6c61))

## [2.4.6](https://github.com/joutio/joutio-identity/compare/v2.4.5...v2.4.6) (2026-11-20)

### Bug Fixes

* **deps:** joutio-parent 2.2.0, kafka consumer, segment and rebalancer metrics ([cef09f7](https://github.com/joutio/joutio-identity/commit/cef09f779da4900c8016a970e53f76c4d31527dd))

## [2.4.5](https://github.com/joutio/joutio-identity/compare/v2.4.4...v2.4.5) (2026-11-19)

### Bug Fixes

* **errors:** typed problems instead of IllegalArgumentException ([b4df757](https://github.com/joutio/joutio-identity/commit/b4df757a9391d7626265867dc120d18b16539998))

## [2.4.4](https://github.com/joutio/joutio-identity/compare/v2.4.3...v2.4.4) (2026-11-18)

### Bug Fixes

* **auth:** wait for the new account to be projected before issuing the session ([88c9627](https://github.com/joutio/joutio-identity/commit/88c96274cfb11cf2e9872e744edc5b9a13a4eb9b))

## [2.4.3](https://github.com/joutio/joutio-identity/compare/v2.4.2...v2.4.3) (2026-11-18)

### Bug Fixes

* **deps:** joutio-parent 2.0.0, idle-friendly processors and bytea Axon schema ([e4444c9](https://github.com/joutio/joutio-identity/commit/e4444c9c1bc143249d18dc7800e2692b5b010251))

## [2.4.2](https://github.com/joutio/joutio-identity/compare/v2.4.1...v2.4.2) (2026-11-17)

### Bug Fixes

* **deps:** joutio-parent 1.2.0, segmented processing groups and single-partition Axon topic ([c74c081](https://github.com/joutio/joutio-identity/commit/c74c081ce527650e2a30b36bc80ab58b903378f1))

## [2.4.1](https://github.com/joutio/joutio-identity/compare/v2.4.0...v2.4.1) (2026-11-17)

### Bug Fixes

* **deps:** joutio-parent 1.1.0, unknown enum constants tolerated on the Axon transport ([4d4a560](https://github.com/joutio/joutio-identity/commit/4d4a560c27b162327b713105fb44bf18fbf519cc))

## [2.4.0](https://github.com/joutio/joutio-identity/compare/v2.3.0...v2.4.0) (2026-11-16)

### Features

* **identity:** hosted sign-in page for the technical tools ([a318041](https://github.com/joutio/joutio-identity/commit/a318041923d499c1f09615e1734c3ff98e483293))

## [2.3.0](https://github.com/joutio/joutio-identity/compare/v2.2.0...v2.3.0) (2026-11-15)

### Features

* **user:** record the acceptance of the terms of use ([47aecce](https://github.com/joutio/joutio-identity/commit/47aeccef9a9913aff63d804b717932d6e91a9bd0))

## [2.2.0](https://github.com/joutio/joutio-identity/compare/v2.1.0...v2.2.0) (2026-11-14)

### Features

* **identity:** fixed login code for load-test players on a reserved domain ([9def813](https://github.com/joutio/joutio-identity/commit/9def8131cd610d1bdffa801ea3d4a9b00ad28411))

## [2.1.0](https://github.com/joutio/joutio-identity/compare/v2.0.0...v2.1.0) (2026-11-14)

### Features

* **identity:** announce the suspension reason code on the suspended-account refusal ([2438b6a](https://github.com/joutio/joutio-identity/commit/2438b6ad7bf7c38d9e418cec71dd1b883f6399e4))

## [2.0.0](https://github.com/joutio/joutio-identity/compare/v1.3.0...v2.0.0) (2026-11-14)

### ⚠ BREAKING CHANGES

* **identity:** remove the referral feature (codes, sponsor attribution, UserReferredEvent)

### Features

* **identity:** remove the referral feature (codes, sponsor attribution, UserReferredEvent) ([acf6cff](https://github.com/joutio/joutio-identity/commit/acf6cffcbad22e49919d8eda299c4f84d88f85f3))

## [1.3.0](https://github.com/joutio/joutio-identity/compare/v1.2.2...v1.3.0) (2026-11-13)

### Features

* **identity:** private birth date with 15-year minimum ([180b169](https://github.com/joutio/joutio-identity/commit/180b1695b16c7f33e37b86b0fd098594451d0795))
* **identity:** referral codes and sponsor attribution ([2a52361](https://github.com/joutio/joutio-identity/commit/2a52361a18cf81612daf734fc55efb7afab48ae9))

## [1.2.2](https://github.com/joutio/joutio-identity/compare/v1.2.1...v1.2.2) (2026-11-12)

### Bug Fixes

* **identity:** guard the injectable entitlements constructor ([e1bdb57](https://github.com/joutio/joutio-identity/commit/e1bdb57466768b5b3f4a82e6fa8792ff1ae8682b))

## [1.2.1](https://github.com/joutio/joutio-identity/compare/v1.2.0...v1.2.1) (2026-11-12)

### Bug Fixes

* **identity:** pick the injectable UserEntitlementsAdapter constructor ([3414fa7](https://github.com/joutio/joutio-identity/commit/3414fa772387d722e56b5f596fd3b272d99ff50d))

## [1.2.0](https://github.com/joutio/joutio-identity/compare/v1.1.0...v1.2.0) (2026-11-12)

### Features

* **identity:** carry Premium entitlement in UserAggregate ([cff7c73](https://github.com/joutio/joutio-identity/commit/cff7c73871f5b39e81ac54a15864c41a318501ef))

## [1.1.0](https://github.com/joutio/joutio-identity/compare/v1.0.3...v1.1.0) (2026-11-08)

### Features

* **identity:** expose effective Premium entitlements ([00205df](https://github.com/joutio/joutio-identity/commit/00205df99cd3cbc2fcef799d5c55afb287e87bab))

## [1.0.3](https://github.com/joutio/joutio-identity/compare/v1.0.2...v1.0.3) (2026-11-04)

### Bug Fixes

* **auth:** refresh of native mobile sessions answered 500 ([caeeece](https://github.com/joutio/joutio-identity/commit/caeeecefdc60e40c828d285ced7df771802c19cc))

## [1.0.2](https://github.com/joutio/joutio-identity/compare/v1.0.1...v1.0.2) (2026-10-27)

### Bug Fixes

* **axon:** use Axon.Events as the default Kafka topic ([a466d98](https://github.com/joutio/joutio-identity/commit/a466d98335627ca2af1108a3f846e14b8f9263a5))

## [1.0.1](https://github.com/joutio/joutio-identity/compare/v1.0.0...v1.0.1) (2026-10-27)

### Bug Fixes

* remove legacy OAuth app defaults ([08b4af8](https://github.com/joutio/joutio-identity/commit/08b4af8a7f1019aec5bb55fe77d623ca311fae10))

## 1.0.0 (2026-10-27)

### Features

* initial Joutio identity service ([ec86c85](https://github.com/joutio/joutio-identity/commit/ec86c85d486ecdbb7617767cfe67460c0ab09885))

## [2.12.0](https://github.com/joutio/joutio-identity/compare/v2.11.0...v2.12.0) (2026-10-22)

### Features

* **identity:** expose UserAccountDeletedEvent dans le contrat identity ([bdd6420](https://github.com/joutio/joutio-identity/commit/bdd6420c34c7d2ba24bc94a0224aeda604ba016b))

## [2.11.0](https://github.com/joutio/joutio-identity/compare/v2.10.1...v2.11.0) (2026-10-22)

### Features

* **account:** suppression définitive du compte avec OTP et effacement des données ([7844f6c](https://github.com/joutio/joutio-identity/commit/7844f6c6d3df38bb42e4f25fd60494965a91c654))

## [2.10.1](https://github.com/joutio/joutio-identity/compare/v2.10.0...v2.10.1) (2026-10-19)

### Bug Fixes

* **identity:** mappe les erreurs Facebook text/javascript en 401 ([76403da](https://github.com/joutio/joutio-identity/commit/76403da6866bc8fb9342de43e9767e73c039ad6e))

## [2.10.0](https://github.com/joutio/joutio-identity/compare/v2.9.0...v2.10.0) (2026-10-19)

### Features

* **identity:** ajoute la connexion Facebook web et mobile ([7fbecec](https://github.com/joutio/joutio-identity/commit/7fbececc527cfa22fc83ab86b687bbf331e5438a))

## [2.9.0](https://github.com/joutio/joutio-identity/compare/v2.8.0...v2.9.0) (2026-10-18)

### Features

* **identity:** expand account suspension reasons ([1d88970](https://github.com/joutio/joutio-identity/commit/1d88970765c2318d6bbc3c1517d07f68255095bf))

## [2.8.0](https://github.com/joutio/joutio-identity/compare/v2.7.1...v2.8.0) (2026-10-18)

### Features

* **identity:** support admin user catalogue filters ([888beea](https://github.com/joutio/joutio-identity/commit/888beeab03d303cb1615162442d8b0cb1d6de490))

## [2.7.1](https://github.com/joutio/joutio-identity/compare/v2.7.0...v2.7.1) (2026-10-18)

### Bug Fixes

* **identity:** harden suspension access revocation ([3ecca51](https://github.com/joutio/joutio-identity/commit/3ecca51534983e6614fbb87f101baad24d6794f1))

## [2.7.0](https://github.com/joutio/joutio-identity/compare/v2.6.0...v2.7.0) (2026-10-18)

### Features

* **identity:** add account moderation and native mobile authentication ([65beb9e](https://github.com/joutio/joutio-identity/commit/65beb9e1efb489bdf11b9d7ede564bb579efddb5))

## [2.6.0](https://github.com/joutio/joutio-identity/compare/v2.5.0...v2.6.0) (2026-10-12)

### Features

* **console:** consume joutio-sdk 4.7.0 (lag, commandes en vol, snapshots, scheduled) ([2bfffa5](https://github.com/joutio/joutio-identity/commit/2bfffa5fb5ffcf22d02ac23b0e7c804881ea877d))

## [2.5.0](https://github.com/joutio/joutio-identity/compare/v2.4.0...v2.5.0) (2026-10-12)

### Features

* **console:** consume joutio-sdk 4.6.0 (build-info + API interne event store/scheduled) ([4888d2b](https://github.com/joutio/joutio-identity/commit/4888d2b27abafb54faf2f382a96bf3a0264d57c8))

## [2.4.0](https://github.com/joutio/joutio-identity/compare/v2.3.1...v2.4.0) (2026-10-12)

### Features

* **console:** consume joutio-sdk 4.5.2 (DLQ Axon par défaut + agent console) ([3b0a681](https://github.com/joutio/joutio-identity/commit/3b0a681dcdc37090f0fff869e2761c581f8738ce))

## [2.3.1](https://github.com/joutio/joutio-identity/compare/v2.3.0...v2.3.1) (2026-10-12)

### Bug Fixes

* **oidc:** redirects console.joutio.local pour le client console (parallèle aux autres clients) ([8d55adc](https://github.com/joutio/joutio-identity/commit/8d55adca46f38d11868a1e5704aed994f7d1ed57))

## [2.3.0](https://github.com/joutio/joutio-identity/compare/v2.2.1...v2.3.0) (2026-10-12)

### Features

* **oidc:** client public console (PKCE) pour la console d'exploitation ([db54608](https://github.com/joutio/joutio-identity/commit/db54608973a44426e1179f1c20e283f597e2bf4c))

## [2.2.1](https://github.com/joutio/joutio-identity/compare/v2.2.0...v2.2.1) (2026-10-11)

### Bug Fixes

* **auth:** preserve third-party authorization request through SPA login ([b64d0f0](https://github.com/joutio/joutio-identity/commit/b64d0f015cbdafbdf0924511d238146575a04c48))

## [2.2.0](https://github.com/joutio/joutio-identity/compare/v2.1.1...v2.2.0) (2026-10-11)

### Features

* **auth:** add OIDC client for ArgoCD ([a37df61](https://github.com/joutio/joutio-identity/commit/a37df61f4c4d6dce10b62dbc1627028d3b8ab116))

## [2.1.1](https://github.com/joutio/joutio-identity/compare/v2.1.0...v2.1.1) (2026-10-11)

### Bug Fixes

* **identity:** sessions web/mobile 365 jours (refresh token) ([b01b787](https://github.com/joutio/joutio-identity/commit/b01b787135aaf103cf218ff18df3efb3472a2bd8))

## [2.1.0](https://github.com/joutio/joutio-identity/compare/v2.0.0...v2.1.0) (2026-10-11)

### Features

* **auth:** emit UserLoggedInEvent on every successful login ([5c1cd79](https://github.com/joutio/joutio-identity/commit/5c1cd7921f8dfb8b2073594a044380c3913e0e39))

## [2.0.0](https://github.com/joutio/joutio-identity/compare/v1.8.3...v2.0.0) (2026-09-25)

### ⚠ BREAKING CHANGES

* **identity:** the user search use case and repository ports now use the
SDK core SearchRequest/SearchResponse DTOs; the previous custom pagination
model is removed.

### Features

* **identity:** migrate to SDK SearchRequest/SearchResponse contracts ([3667676](https://github.com/joutio/joutio-identity/commit/366767661ca62661d6473170650e60570bc1fc5e))

## [1.8.3](https://github.com/joutio/joutio-identity/compare/v1.8.2...v1.8.3) (2026-09-24)

### Bug Fixes

* **deps:** bump joutio-parent to 2.4.4 (typed PageResult over query transport) ([fa62722](https://github.com/joutio/joutio-identity/commit/fa627226c65b0383b2c8cccdd256e8a7ab318cf4))

## [1.8.2](https://github.com/joutio/joutio-identity/compare/v1.8.1...v1.8.2) (2026-09-24)

### Bug Fixes

* **deps:** bump joutio-parent to 2.4.3 (bus-only search criteria type info) ([8e6f4c6](https://github.com/joutio/joutio-identity/commit/8e6f4c62ade6c624f9d928c0154402e0f812174b))

## [1.8.1](https://github.com/joutio/joutio-identity/compare/v1.8.0...v1.8.1) (2026-09-24)

### Bug Fixes

* **deps:** bump joutio-parent to 2.4.2 (search criteria type info) ([d81db46](https://github.com/joutio/joutio-identity/commit/d81db46a4541be51e4c32ebd624d11a03ea4f37e))

## [1.8.0](https://github.com/joutio/joutio-identity/compare/v1.7.4...v1.8.0) (2026-09-23)

### Features

* **identity:** passwordless auth as resources (login-codes/sessions) and bump SDK to 2.4.1 ([0ee48aa](https://github.com/joutio/joutio-identity/commit/0ee48aa24d9d9323b85576844a2dec7885499fd0))

## [1.7.4](https://github.com/joutio/joutio-identity/compare/v1.7.3...v1.7.4) (2026-09-22)

### Bug Fixes

* **joutio-identity:** upgrade joutio-parent to 2.3.4 ([2b0c65a](https://github.com/joutio/joutio-identity/commit/2b0c65a5c6906a02dadc703a0a2f803364cd05d8))

## [1.7.3](https://github.com/joutio/joutio-identity/compare/v1.7.2...v1.7.3) (2026-09-22)

### Bug Fixes

* **joutio-identity:** upgrade joutio-parent to 2.3.3 ([9729991](https://github.com/joutio/joutio-identity/commit/9729991ce1a6fad3d0a439a05bdc07f83fa438c6))

## [1.7.2](https://github.com/joutio/joutio-identity/compare/v1.7.1...v1.7.2) (2026-09-22)

### Bug Fixes

* **joutio-identity:** upgrade joutio-parent to 2.3.2 ([cee392b](https://github.com/joutio/joutio-identity/commit/cee392b9a74d63b3c02822696f769c3fc8e8e5ac))

## [1.7.1](https://github.com/joutio/joutio-identity/compare/v1.7.0...v1.7.1) (2026-09-22)

### Bug Fixes

* **identity:** upgrade joutio-parent to 2.3.1 (registration address fix) ([164750e](https://github.com/joutio/joutio-identity/commit/164750ebe28e6f4354e8714a7a2f574758aabbab))

## [1.7.0](https://github.com/joutio/joutio-identity/compare/v1.6.0...v1.7.0) (2026-09-22)

### Features

* **identity:** public-client refresh tokens, explicit Axon processing groups and default seeding ([4a5dec9](https://github.com/joutio/joutio-identity/commit/4a5dec9aa17621707e59e8e47dd50ad33de36281))

## [1.6.0](https://github.com/joutio/joutio-identity/compare/v1.5.3...v1.6.0) (2026-09-21)

### Features

* **identity:** atomic email reservation against concurrent sign-ups ([9b096b6](https://github.com/joutio/joutio-identity/commit/9b096b66f223536a81019073e99f672684c45866))
* **identity:** extend token lifetimes for web, mobile and grafana ([92ab472](https://github.com/joutio/joutio-identity/commit/92ab472b790bb6b62cb0e2f26a30150c43788750))

### Bug Fixes

* **observability:** keep readable console logs in local ([8b1de91](https://github.com/joutio/joutio-identity/commit/8b1de916f578e73b1c604a833373780b57aa7eaa))

## [1.5.3](https://github.com/joutio/joutio-identity/compare/v1.5.2...v1.5.3) (2026-09-20)

### Bug Fixes

* **deps:** bump joutio-sdk to 2.1.1 ([4216592](https://github.com/joutio/joutio-identity/commit/42165925f794ba268d5fa5e718267345e7f07f01))

## [1.5.2](https://github.com/joutio/joutio-identity/compare/v1.5.1...v1.5.2) (2026-09-20)

### Bug Fixes

* **deps:** bump joutio-sdk to 2.1.0 ([faf051b](https://github.com/joutio/joutio-identity/commit/faf051ba4d468fbb277166f78cfdd5eda78b4d29))

## [1.5.1](https://github.com/joutio/joutio-identity/compare/v1.5.0...v1.5.1) (2026-09-20)

### Bug Fixes

* **deps:** bump joutio-sdk to 2.0.0 ([4d89800](https://github.com/joutio/joutio-identity/commit/4d89800c92bdc5915d875749772c27589efc8d13))

## [1.5.0](https://github.com/joutio/joutio-identity/compare/v1.4.3...v1.5.0) (2026-09-20)

### Features

* **auth:** replace password with email OTP and email-based admin role ([9699400](https://github.com/joutio/joutio-identity/commit/9699400e0a3607e4160fe74343686ed0228c27de))

## [1.4.3](https://github.com/joutio/joutio-identity/compare/v1.4.2...v1.4.3) (2026-09-20)

### Bug Fixes

* **observability:** remove WebSocket and business KPI metrics (consume joutio-sdk 1.4.3) ([9e8a7c3](https://github.com/joutio/joutio-identity/commit/9e8a7c3a6943f8bfee62c2cee1f55829e35c8d89))

## [1.4.2](https://github.com/joutio/joutio-identity/compare/v1.4.1...v1.4.2) (2026-09-20)

### Bug Fixes

* **observability:** consume joutio-sdk 1.4.2 (Axon activity metrics fix + Swagger server URL) ([0a40edf](https://github.com/joutio/joutio-identity/commit/0a40edf82b4eaf11b8891487b9308e51f84580ad))

## [1.4.1](https://github.com/joutio/joutio-identity/compare/v1.4.0...v1.4.1) (2026-09-20)

### Bug Fixes

* **swagger:** allow the gateway Swagger UI redirect URIs ([942e9e7](https://github.com/joutio/joutio-identity/commit/942e9e717d36c6bfd551e5c2ed0724401c1617a5))

## [1.4.0](https://github.com/joutio/joutio-identity/compare/v1.3.0...v1.4.0) (2026-09-20)

### Features

* **observability:** consume joutio-sdk 1.4.0 (Axon activity metrics) ([6b9d2c2](https://github.com/joutio/joutio-identity/commit/6b9d2c2c461f888c3526b9a44a182c1398f4450f))

## [1.3.0](https://github.com/joutio/joutio-identity/compare/v1.2.0...v1.3.0) (2026-09-20)

### Features

* **observability:** consume joutio-sdk 1.3.0 (WebSocket metrics) ([a8bdd1e](https://github.com/joutio/joutio-identity/commit/a8bdd1e725635544ce63e924418eb2ac4ed2c9cc))

## [1.2.0](https://github.com/joutio/joutio-identity/compare/v1.1.0...v1.2.0) (2026-09-20)

### Features

* **observability:** emit ROLE_ADMIN and auth KPIs (consume joutio-sdk 1.2.0) ([b111edb](https://github.com/joutio/joutio-identity/commit/b111edbc0f9ee092265a08140b4e0364de16887e))

## [1.1.0](https://github.com/joutio/joutio-identity/compare/v1.0.1...v1.1.0) (2026-09-20)

### Features

* **observability:** add Grafana OIDC client and consume joutio-sdk 1.1.0 ([806aff6](https://github.com/joutio/joutio-identity/commit/806aff6a5e588d4dd9ac3d7d511d0d8efca4aa3a))

## [1.0.1](https://github.com/joutio/joutio-identity/compare/v1.0.0...v1.0.1) (2026-09-19)

### Bug Fixes

* **oidc:** allow the production web redirect URI (app.joutio.com/callback) ([25ec42b](https://github.com/joutio/joutio-identity/commit/25ec42b66a762be4b663d4184efda945aaf165d9))

## 1.0.0 (2026-09-19)

### Features

* first commit ([f162735](https://github.com/joutio/joutio-identity/commit/f162735c7ca8bd1a20463afe1a70994f0f202d84))

### Bug Fixes

* **maven:** pin inter-service domains to 0.0.1; align parent to joutio-parent 1.0.0 ([a8d10d4](https://github.com/joutio/joutio-identity/commit/a8d10d41b8e2c7745dc514ba7b24af456db9b67c))
