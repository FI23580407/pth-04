/*
 * ExtractFields plugin for Pentaho PTH-04
 * Copyright (C) 2021  Fail-Safe IT Solutions Oy
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  
 *  
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *  
 * If you modify this Program, or any covered work, by linking or combining it 
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Fail-Safe IT Solutions Oy without any 
 * additional modifications.
 *  
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *  
 * Origin of the software must be attributed to Fail-Safe IT Solutions Oy. 
 * Any modified versions must be marked as "Modified version of" The Program.
 *  
 * Names of the licensors and authors may not be used for publicity purposes.
 *  
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *  
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *  
 */

#include <stddef.h>
#include <string.h>
#include <liblognorm.h>
#include <lognorm.h>

/*
 * FIXME add real error handling
 */

void *init(char *repository) {
    ln_ctx *ctx = malloc(sizeof(ln_ctx));

    // init liblognorm context
    if((*ctx = ln_initCtx()) == NULL) {
        return NULL;
    }
    
    // check if parse rule string null
    if(repository == NULL) {
        return NULL;
    }

    // load parse rule from string
    if(ln_loadSamplesFromString(*ctx, repository)) {
        return NULL;
    }

    return ctx;
}

void *normalize(ln_ctx *context, char *line) {
    ln_ctx ctx = *context;
    struct json_object *json = NULL;
    // run
    ln_normalize(ctx, line, strlen(line), &json);

    if(json != NULL) {
        return json;
    }

}

char *read_result(struct json_object *jref) {
    return (char*)json_object_to_json_string(jref);
}

void destroy_result(struct json_object *jref) {
    json_object_put(jref);
}

void deinit(ln_ctx *context) {
    if (*context) {
        ln_exitCtx(*context);
    }
    free(context);
}

/*
int main(void) {
    void *ctx;
    void *jref;
    char *res;
    ctx = init("rule=:%all:rest%");
    jref = normalize(ctx, "offline");
    res = read_result(jref);
    printf("%s\n", res); // only referenceable before destroy
    destroy_result(jref);
    deinit(ctx);
    return 0;
}
*/
