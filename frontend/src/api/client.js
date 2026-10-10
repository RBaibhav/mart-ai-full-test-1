import { createAiApi } from "./aiApi";
import { createAuthApi } from "./authApi";
import { createCatalogApi } from "./catalogApi";
import { createOperationsApi } from "./operationsApi";
import { request } from "./http";
import { createShoppingApi } from "./shoppingApi";

export const api = {
  ...createAuthApi(request),
  ...createCatalogApi(request),
  ...createShoppingApi(request),
  ...createOperationsApi(request),
  ...createAiApi(request),
};
