precision mediump float;
uniform float uR;
varying vec2 mcLongLat;//���մӶ�����ɫ�������Ĳ���
varying vec3 vPosition;//���մӶ�����ɫ�������Ķ���λ��
void main()                         
{
   vec3 color;
   float n = 8.0;//һ����������ֵ��ܷ���
   float span = 2.0*uR/n;//ÿһ�ݵĳ���
   //ÿһά���������ڵ�������
   int i = int((vPosition.x + uR)/span);
   int j = int((vPosition.y + uR)/span);
   int k = int((vPosition.z + uR)/span);
   //���㵱��Ӧλ�ڰ�ɫ�黹�Ǻ�ɫ����
   int whichColor = int(mod(float(i+j+k),2.0));
   if(whichColor == 1) {//����ʱΪ��ɫ
   		color = vec3(0.678,0.231,0.129);//��ɫ
   }
   else {//ż��ʱΪ��ɫ
   		color = vec3(0.23,0.456,0.789);//��ɫ
   }
   //�����������ɫ����ƬԪ
   gl_FragColor=vec4(color,0);
}     