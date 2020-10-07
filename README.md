1.	Opis działania aplikacji.
Po uruchomieniu aplikacji pojawia się okno do wybrania przy użyciu suwaka liczby atomów (od 0 do 1000), które mają brać udział w animacji.
 
Po wciśnięciu przycisku Start pojawia się animacja ruchu atomów. Atomy odbijają się od siebie nawzajem i od ścianek „pudełka”, w którym się znajdują – zarówno poziomych, jak i pionowych. Kolory kulek zostały dobrane losowo.

Po naciśnięciu przycisku Stop animacja zostaje zatrzymana, a użytkownik ma możliwość wyświetlenia wykresów energii w funkcji czasu lub ponownego wybrania parametrów do animacji.
 
Po wciśnięciu przycisku Charts wyświetlone zostają wykresy energii w funkcji czasu. Na wykresie przedstawione są cztery serie dotyczące energii kinetycznej, potencjalnej, energii sprężystości oraz energii całkowitej układu.

2.	Kod

W ramach projektu utworzone zostały 4 klasy funkcjonalne: 
a.	MD, której obiekty imitują poruszające się atomy; zaimplementowane są w niej metody calculateAcceleration(), obliczająca chwilowe przyspieszenia cząstek, energię potencjalną i energię potencjalną sprężystości, Verlet(), przyjmującą za parametr długość kroku całkowania, która oblicza kolejne położenia i prędkości wzdłuż obu osi, calculateKineticEnergy(), obliczającą energię kinetyczną cząstek i initialValues(), która generuje losowe położenia początkowe dla wszystkich cząstek;
b.	Scale, której jedyna metoda przyjmuje za parametry fizyczne położenia cząstek, wielkość okna graficznego i wielkość okna fizycznego i zwraca tablicę zawierającą nowe współrzędne ekranowe cząstek obliczone za pomocą proporcji;
c.	Animation, która wyświetla ruch cząsteczek za pomocą metody relocate() oraz w nowym oknie wyświetla wykresy energii obliczonych w klasia MD;
d.	Launch, której zadaniem jest otworzenie okna animacji klasy Animation i wyświetlenie zawartości;

