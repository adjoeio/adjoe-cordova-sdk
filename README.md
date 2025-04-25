This project is the property of adjoe GmbH and is published for the sole use of entities with which adjoe has a contractual agreement.
The unauthorized redistribution of any or all parts of this project is strictly prohibited.

# Add Cordova SDK to your app.

To integrate the adjoe Cordova SDK into your Cordova project, follow these steps:

1. Open your project's `package.json` file.

2. Add the adjoe SDK as a dependency under the `dependencies` section. You can change the version number to the desired version of adjoe SDK you want to integrate.

```yaml
dependencies: {
  "cordova-plugin-adjoe": "https://github.com/adjoeio/adjoe-cordova-sdk#v3.1.0"
}
```
3. save the `package.json` and run the following command:
```
npm install
```
