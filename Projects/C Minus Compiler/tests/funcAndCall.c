int my_func(){
	int a;
	a = 5;
	return a;
}

int main(){

	int b;
	int c;
	b = 3;
	c = 4;
	b = b + c;
	c = my_func() + 3;
	return 0;
}
