# You-Get Wrapper
This simple tool can help you use [You-Get](https://github.com/soimort/you-get) more conveniently. It supports downloading multiple URLs simultaneously and put them into separate folders named after corresponding titles or into a single folder based on your instruction.

## Features:
* No need to change source code after you update You-Get
* Manage URLs in target list
* Fetch and show titles of targets (with cache)
* Manage target list using a json file
* Download all targets into a single folder
* Download all targets into separate folders named after their titles
* Allow users to specify the quality level of targets to be downloaded
* Multiple targets can be downloaded at the same time
* Report any failed targets to the user
* Manage downloading settings using a json file

## Dependencies
[Gson](https://github.com/google/gson) is used to serialize and deserialize Java Objects into JSON and back.

## Usage
Change the values of constants in the Controller class before you run it. You can get You-Get from [here](https://github.com/soimort/you-get).

If you are using the .exe executable version of You-Get, please set LOCATION variable to be the directory path which contains your .exe file and set `PORTABLE = true`.

If you have installed You-Get through a package manager, please set `LOCATION = "you-get"` and `PORTABLE = false`.

## License
This software is distributed under the [MIT license](https://github.com/ad52825196/you-get-wrapper/raw/master/LICENSE).
