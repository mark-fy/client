uniform sampler2D baseTexture;
varying vec2 texCoords;

void main() {
    vec2 uv = texCoords.xy;
    vec2 offset = 1.0 / textureSize(baseTexture, 0);

    // Apply a simple separable Gaussian blur
    vec3 blur = vec3(0.0);
    for (int i = -4; i <= 4; ++i) {
        blur += texture2D(baseTexture, uv + vec2(float(i) * offset.x, 0.0)).rgb;
    }
    blur /= 9.0;

    gl_FragColor = vec4(blur, 1.0);
}