#version 120

attribute vec4 Position;
attribute vec2 TexCoord;

varying vec2 fragTexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * Position;
    fragTexCoord = TexCoord;
}