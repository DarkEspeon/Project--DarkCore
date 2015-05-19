#version 330

in vec2 texCoord;

out vec4 col;

uniform sampler2D guiTex;

void main(){
	col = texture(guiTex, texCoord);
}