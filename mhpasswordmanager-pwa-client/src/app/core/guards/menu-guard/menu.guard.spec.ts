import { TestBed } from '@angular/core/testing';

import { MenuGuard } from './menu.guard';

describe('MenuGuard', () => {
  let guard: MenuGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(MenuGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
