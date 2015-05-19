#version 330

in vec2 pos;

out vec2 texCoord;

uniform mat4 transMat;

void main(){
	gl_Position = transMat * vec4(pos, 0.0, 1.0);
	texCoord = vec2((pos.x + 1.0) / 2.0, 1 - (pos.y + 1.0) / 2.0);
}