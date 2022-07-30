import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { MhAvatarUserPrincipalComponent } from './mh-avatar-user-principal.component';

describe('MhAvatarUserPrincipalComponent', () => {
  let component: MhAvatarUserPrincipalComponent;
  let fixture: ComponentFixture<MhAvatarUserPrincipalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ MhAvatarUserPrincipalComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(MhAvatarUserPrincipalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
