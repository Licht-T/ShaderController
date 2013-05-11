ShaderController
======================

Description
------
A simple GLSL shader program controller.

Requirement
------
* [JOGL](https://jogamp.org/jogl/www/)

Usage
------
#### 1. Create an object ####
    shader = new ShaderController(gl //GL2 );

#### 2. Set codes ####
    shader.loadShaderFiles("program.vert", "program.frag");

#### 3. Compile and attach ####
    shader.shaderInit();

#### 4. Enable the shader  program ####
    shader.enable();
    
#### 5. Set a parameter to a variable in the shader program   ####
    int loc_of_var= shader.glGetUniformLocation("var" //float );
    gl.glUniform1i(loc_of_var, 1.0);

#### 6. Disable the shader  program ####
    shader.disable();

License
----------
Copyright &copy; 2011 ShaderController
Distributed under the [MIT License][mit].

[MIT]: http://www.opensource.org/licenses/mit-license.php
