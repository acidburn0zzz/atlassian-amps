Bootstrap your frontend with Webpack, TypeScript, React and React Testing Library

### Developing the plugin

In the project directory, you can run:

#### `yarn start`

It builds the frontend and puts it in the watch mode with hot reload. 
In other words, if you have the whole plugin and an instance already working, 
this will enable you to make quick changes with instant preview.

### Before you git push

Any unit tests or eslint errors will cause the build to fail, 
so it's worth checking these before you push to branch.

#### `yarn test`

For running UI tests.

#### `yarn lint`

Checks the frontend plugin for styling errors. 

The ruleset is set to be compatible with other Server plugins, 
so please mind that when considering making changes to it.

#### `yarn lint --fix`

Will additionally fix any automatically-fixable issues.