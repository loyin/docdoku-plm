(function () {

    'use strict';

    angular.module('dplm.services')
        .service('FolderService', function ($window, uuid4, $q, $filter) {

            var _this = this;

            var glob = $window.require('glob');
            var fs = $window.require('fs');
            var ignoreList = ['.dplm'];

            this.folders = angular.fromJson($window.localStorage.folders || '[]');

            var alreadyHave = function (path) {
                return _this.folders.filter(function (folder) {
                        return folder.path == path;
                    }).length > 0;
            };

            this.getFolder = function (params) {
                return $filter('filter')(_this.folders, params)[0];
            };

            this.add = function (path) {
                if (alreadyHave(path)) {
                    return;
                }
                _this.folders.push({
                    uuid: uuid4.generate(),
                    path: path,
                    favorite: false,
                    localChanges: 0
                });
                _this.save();
            };

            this.delete = function (folder) {
                if (alreadyHave(folder.path)) {
                    _this.folders.splice(_this.folders.indexOf(folder), 1);
                    _this.save();
                }
            };

            this.save = function () {
                $window.localStorage.folders = angular.toJson(_this.folders);
            };

            this.removeFolders = function () {
                _this.folders.length = 0;
                _this.save();
            };

            this.recursiveReadDir = function (path) {
                return $q(function (resolve, reject) {
                    var recursive = $window.require('recursive-readdir');
                    recursive(path, ignoreList, function (err, files) {
                        if (err) {
                            reject(err);
                        }
                        else {
                            resolve(files);
                        }
                    });
                });
            };

            this.getFilesCount = function (path) {
                return $q(function (resolve, reject) {
                    var recursive = $window.require('recursive-readdir');
                    recursive(path, ignoreList, function (err, files) {
                        if (err) {
                            reject(err);
                        }
                        else {
                            resolve(files.length);
                        }
                    });
                });
            };

            this.reveal = function (path) {
                var os = $window.require('os');
                var command = '';
                switch (os.type()) {
                    case 'Windows_NT' :
                        command = 'explorer';
                        break;
                    case 'Darwin' :
                        command = 'open';
                        break;
                    default :
                        command = 'nautilus';
                        break;
                }
                $window.require('child_process').spawn(command, [path]);

            };

            this.shell = function (path) {
                var cmd;
                switch ($window.process.platform) {
                    case 'darwin' :
                        cmd = 'open /Applications/Utilities/Terminal.app/';
                        break;
                    case 'win32' :
                    case 'win64' :
                        cmd = 'cmd';
                        break;
                    case 'linux' :
                        cmd = 'x-terminal-emulator';
                        break;
                    default:
                        break;
                }
                if (!cmd) {
                    // Should use toast service, action is not available for this platform
                } else {
                    $window.require('child_process').exec(cmd, {
                        cwd: path
                    }, function (error, stdout, stderr) {

                    });
                }
            };

            this.isFolder = function (path) {
                return $q(function (resolve, reject) {
                    var fs = require('fs');
                    fs.stat(path, function (err, stats) {
                        if (err) {
                            reject(err);
                        }
                        else if (stats.isDirectory()) {
                            resolve();
                        } else {
                            reject(null);
                        }
                    });
                });
            };

            this.createFileObject = function (path) {
                return {
                    path: path
                };
            };

        });

})();
