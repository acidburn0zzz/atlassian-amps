const WrmPlugin = require("atlassian-webresource-webpack-plugin");
const DuplicatePackageCheckerPlugin = require("duplicate-package-checker-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const PostCssIcssValuesPlugin = require("postcss-icss-values");
const path = require("path");
const TerserPlugin = require("terser-webpack-plugin");
const merge = require("webpack-merge");
const webpack = require("webpack");

const PLUGIN_KEY = process.env.PLUGIN_KEY;
const ROOT_DIR = path.join(__dirname, ".");
const SRC_DIR = path.join(ROOT_DIR, "src", "main");
const I18N_DIR = path.join(SRC_DIR, "resources");
const FRONTEND_SRC_DIR = path.join(SRC_DIR, "app");
const FRONTEND_OUTPUT_DIR = path.join(ROOT_DIR, "target", "classes");
const ENTRY_POINT = {
    "atlassian-frontend-bootstrap": path.join(FRONTEND_SRC_DIR, "index.tsx")
};
const I18N_FILES = ["app.properties"].map(file =>
    path.join(I18N_DIR, "i18n", file)
);
const WRM_OUTPUT = path.resolve(
    "./",
    "target",
    FRONTEND_OUTPUT_DIR,
    "META-INF",
    "plugin-descriptors",
    "wr-webpack-bundles.xml"
);
const DEV_SERVER_PORT = 3333;
const DEV_SERVER_HOST = "localhost";

const watchConfig = {
    devServer: {
        host: DEV_SERVER_HOST,
        port: DEV_SERVER_PORT,
        overlay: true,
        hot: true,
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers": "*"
        }
    },
    plugins: [new webpack.HotModuleReplacementPlugin()],
    devtool: "inline-source-map"
};

const developmentConfiguration = env => {
    return merge([
        {
            optimization: {
                minimize: false,
                runtimeChunk: false,
                splitChunks: false
            },
            output: {
                publicPath: `http://${DEV_SERVER_HOST}:${DEV_SERVER_PORT}/`,
                filename: "[name].js",
                chunkFilename: "[name].chunk.js"
            }
        },
        env === "dev-server" && watchConfig
    ]);
};

const productionConfig = {
    optimization: {
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    mangle: {
                        reserved: ["I18n", "getText"]
                    }
                }
            })
        ]
    },
    output: {
        filename: "bundled.main.js"
    }
};

module.exports = (env, argv = {}) => {
    const isProduction = argv.mode !== "development";
    const modeConfig = isProduction
        ? productionConfig
        : developmentConfiguration(env);
    return merge([
        {
            mode: argv.mode,
            entry: ENTRY_POINT,
            resolve: {
                extensions: ["*", ".ts", ".tsx", ".js", ".jsx"]
            },
            stats: {
                logging: "info"
            },
            context: FRONTEND_SRC_DIR,
            plugins: [
                new WrmPlugin({
                    pluginKey: PLUGIN_KEY,
                    xmlDescriptors: WRM_OUTPUT,
                    providedDependencies: {
                        "wrm/context-path": {
                            dependency:
                                "com.atlassian.plugins.atlassian-plugins-webresource-plugin:context-path",
                            import: {
                                var: "require('wrm/context-path')",
                                amd: "wrm/context-path"
                            }
                        },
                        "wrm/format": {
                            dependency:
                                "com.atlassian.plugins.atlassian-plugins-webresource-plugin:format",
                            import: {
                                var: 'require("wrm/format")',
                                amd: "wrm/format"
                            }
                        }
                    },
                    singleRuntimeWebResourceKey: "frontend-plugin",
                    watch: !isProduction,
                    watchPrepare: !isProduction
                }),
                new DuplicatePackageCheckerPlugin()
            ],
            module: {
                rules: [
                    {
                        test: /\.jsx?$/,
                        exclude: /node_modules/,
                        use: [
                            {
                                loader: "@atlassian/i18n-properties-loader",
                                options: {
                                    i18nFiles: I18N_FILES,
                                    disabled: isProduction
                                }
                            },
                            {
                                loader: "babel-loader",
                                options: {
                                    cacheDirectory: true
                                }
                            }
                        ]
                    },
                    {
                        test: /\.less$/,
                        use: [
                            {
                                loader: isProduction
                                    ? MiniCssExtractPlugin.loader
                                    : "style-loader",
                                options: {
                                    sourceMap: true
                                }
                            },
                            {
                                loader: "css-loader",
                                options: {
                                    sourceMap: true
                                }
                            },
                            {
                                loader: "postcss-loader",
                                options: {
                                    sourceMap: true,
                                    plugins: [new PostCssIcssValuesPlugin()]
                                }
                            },
                            {
                                loader: "less-loader",
                                options: {
                                    sourceMap: true
                                }
                            }
                        ]
                    },
                    {
                        test: /\.(png|jpg|gif|svg)$/,
                        use: [
                            {
                                loader: "file-loader",
                                options: {}
                            }
                        ]
                    },
                    {
                        test: /\.soy$/,
                        use: [
                            ...((!isProduction && [
                                    {
                                        loader: "@atlassian/i18n-properties-loader",
                                        options: {
                                            I18N_FILES
                                        }
                                    }
                                ]) ||
                                []),
                            {
                                loader: "@atlassian/soy-loader",
                                options: {
                                    dontExpose: true
                                }
                            }
                        ]
                    },
                    {test: /\.tsx?$/, loader: "awesome-typescript-loader"}
                ]
            },
            output: {
                path: FRONTEND_OUTPUT_DIR
            }
        },
        modeConfig
    ]);
};
