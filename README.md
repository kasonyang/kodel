# Overview

Kodel is a text file generator.

# Installation

1. Download the [latest release](https://github.com/kasonyang/kodel/releases) and unpack it.
2. Add the absolute path of the `bin` directory to the `PATH` environment variable.

# Usage

## step1:define a template(using [tempera](http://kason.site/projects/tempera) engine):`templates/hello.tpr`

    {{var name:String}}
    public class {{name}} {
      public static void main(String[] args){
        System.out.println("Hello,{{name}}!");
      }
    }

## step2:create a build script(groovy script):`hello.kodel`

    def model = [name:'World']
    template(model,'hello.tpr','build/World.java')

## step3:execute the build script

    kodel hello.kodel

After step3,a file named `World.java` will be generated in directory `build`.

# Supported template engine and file suffix

* [tempera](http://kason.site/projects/tempera) : `.tpr`
* [handlebars](https://jknack.github.io/handlebars.java) : `.hbs`
* [pebble](https://github.com/PebbleTemplates/pebble) : `.pbl`

