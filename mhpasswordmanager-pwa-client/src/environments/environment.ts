// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  clientId: '92a67091-4264-4ce4-8fcb-3ec6dbfeea16',
  clientSecret: 'fd04f93e-5e4d-4f16-98ae-9247f68d8619',
  authorizationServerUrl: 'http://localhost:9000',
  apiGatewayUrl: 'http://localhost:8765'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/dist/zone-error';  // Included with Angular CLI.
