/*
 * MIT License
 *
 * Copyright (c) 2014-2022, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 * and Pedro Fialho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow.visitor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.typeManufacturers.TypeManufacturer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static htmlflow.visitor.TypeFactories.singleTypeArgument;

class TypeFactories {

    private TypeFactories() {
    }

    public static Class<?> singleTypeArgument(Map<String, Type> map) {
        Optional<Type> typeArg = map.values().stream().findFirst();
        if(!typeArg.isPresent()) throw new IllegalArgumentException("Missing type argument in  generic collection!");
        else return (Class<?>) typeArg.get();
    }
    
    public static List<Class<?>> typeArgument(Map<String, Type> map) {
        return map.values().stream().map(v -> (Class<?>) v).collect(Collectors.toList());
    }
}

@SuppressWarnings("squid:S3740")
class PublisherFactory implements TypeManufacturer<Publisher> {
    final PodamFactory podamFactory;

    PublisherFactory(PodamFactory podamFactory) {
        this.podamFactory = podamFactory;
    }

    @Override
    public Publisher getType(DataProviderStrategy strat, AttributeMetadata attributeMetadata, Map<String, Type> map) {
        return subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long l) {
                Class<?> typeArg = singleTypeArgument(map);
                int size = strat.getNumberOfCollectionElements(List.class);
                for (int i = 0; i < size; i++) {
                    Object item = podamFactory.manufacturePojoWithFullData(typeArg);
                    subscriber.onNext(item);
                }
                subscriber.onComplete();

            }
            @Override
            public void cancel() {
                /*
                 * The request() starts emitting to the subscriber in same thread from the caller,
                 * without chance for cancellation.
                 */
            }
        });
    }
}

@SuppressWarnings("squid:S3740")
class StreamFactory implements TypeManufacturer<Stream> {
    final PodamFactory podamFactory;

    StreamFactory(PodamFactory podamFactory) {
        this.podamFactory = podamFactory;
    }

    @Override
    public Stream getType(DataProviderStrategy strat, AttributeMetadata attributeMetadata, Map<String, Type> map) {
        int size = strat.getNumberOfCollectionElements(List.class);
        Class<?> typeArg = singleTypeArgument(map);
        return Stream
            .generate(() -> podamFactory.manufacturePojoWithFullData((Class) typeArg))
            .limit(size);
    }
}

@SuppressWarnings("squid:S3740")
class IterableFactory implements TypeManufacturer<Iterable> {
    final StreamFactory streamFactory;

    IterableFactory(PodamFactory podamFactory) {
        this.streamFactory = new StreamFactory(podamFactory);
    }

    @Override
    public Iterable getType(DataProviderStrategy strat, AttributeMetadata metadata, Map<String, Type> map) {
        return () -> streamFactory.getType(strat, metadata, map).iterator();
    }

}
