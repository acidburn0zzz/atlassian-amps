import React, { ReactElement } from 'react';

interface AppProps {
    example: string;
}

export function App(props: AppProps): ReactElement {
    const { example } = props;
    return <h1>{example}</h1>;
}
