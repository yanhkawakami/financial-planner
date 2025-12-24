export interface Spend {
  id?: number;
  spendDate: string;
  spendValue: number;
  description: string;
  categoryId: number;
  userId: number;
}

export interface SpendUpdate {
  id?: number;
  spendDate: string;
  spendValue: number;
  description: string;
  categoryId: number;
}

export interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
