#version 120

varying vec2 fragTexCoord;

uniform sampler2D baseTexture;

void main() {
    vec3 col = texture2D(baseTexture, fragTexCoord).rgb;

    // Apply bloom effect (modify as needed)
    col = col * 1.2; // Example: Increase brightness

    gl_FragColor = vec4(col, 1.0);
}