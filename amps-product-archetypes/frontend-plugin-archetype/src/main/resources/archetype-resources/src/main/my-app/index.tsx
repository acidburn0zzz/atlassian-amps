import React from 'react';
import ReactDOM from 'react-dom';
import whenDomReady from 'when-dom-ready';
import { hot } from 'react-hot-loader';
// eslint-disable-next-line import/no-unresolved
import * as formatter from '@atlassian/wrm-react-i18n';

import { App } from './components/App';

whenDomReady().then(function example() {
    const container = document.getElementById('rate-limit-app-container');
    // eslint-disable-next-line no-undef
    const MyApp = hot(module)(() => <App example={formatter.I18n.getText('myApp.title')} />);
    ReactDOM.render(<MyApp />, container);
});
