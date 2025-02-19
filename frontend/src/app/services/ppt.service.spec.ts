import { TestBed } from '@angular/core/testing';

import { PptService } from './ppt.service';

describe('PptService', () => {
  let service: PptService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PptService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
