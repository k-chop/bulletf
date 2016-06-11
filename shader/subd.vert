#version 140

uniform mat4 modelMatrix;
uniform mat4 projMatrix;

in vec2 in_Position; // x, y
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main(void) {
  gl_Position = vec4(in_Position, 0.0, 1.0);
  gl_Position = modelMatrix * gl_Position;

  pass_Color = in_Color;
  pass_TextureCoord = in_TextureCoord;
}
