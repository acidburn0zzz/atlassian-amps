import React from 'react';
import ReactDOM from 'react-dom';
import whenDomReady from 'when-dom-ready';
import { hot } from 'react-hot-loader';
import * as formatter from '@atlassian/wrm-react-i18n';

import { App } from './components/App';

whenDomReady().then(function example() {
    const container = document.getElementById('app-container');
    const MyApp = hot(module)(() => <App example={formatter.I18n.getText('app.title')} />);
    ReactDOM.render(<MyApp />, container);
});
