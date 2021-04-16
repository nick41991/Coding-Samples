int main(){
	int a;
LABEL:
	a = 5;
 	a = 6;
	if(a == (5 - 1)){
		int b;
		b = 1;
		a = 6;
	} else {
		a = 4;
	}

	while(1 == 1){
		a = a + 1;
	}

	goto TOP;

}
