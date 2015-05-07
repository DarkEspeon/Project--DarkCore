#version 330

in vec2 texCoord0;
in vec3 norm0;
in vec3 pos0;

uniform sampler2D tex;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 norm;
layout (location = 2) out vec4 pos;

void main(){
	pos = vec4(pos0, 1.0);
	color = texture(tex, texCoord0);
	norm = vec4(norm0, 1.0);
}