# Entidade que representa a base de dados das vagas de hospedagem para o lado do cliente e da agência.
class AccommodationEntity
    attr_reader :id
    attr_reader :location, :hotel_name
    attr_reader :entrance_date, :exit_date
    attr_reader :num_rooms, :num_people, :price

    def initialize(accommodation_id: nil, location:, hotel_name:,
        entrance_date:, exit_date:, num_rooms:, num_people:, price:)
        @id = accommodation_id
        @location = location
        @hotel_name = hotel_name
        @entrance_date = entrance_date
        @exit_date = exit_date
        @num_rooms = num_rooms
        @num_people = num_people
        @price = price
    end

    def json_converter
        hash = {}

        hash['location'] = @location
        hash['hotel'] = @hotel_name
        hash['entranceDate'] = @entrance_date
        hash['exitDate'] = @exit_date
        hash['numRooms'] = @num_rooms
        hash['numPeople'] = @num_people
        hash['price'] = @price

        return hash.to_json
    end

    def string_converter
        str = ""

        str += "-----------------------------------\n"
        str += "Oferta de vaga de hotel \##{@id}\n"
        str += "  Localização: #{@location}\n"
        str += "  Nome do hotel: #{@hotel_name}\n"
        str += "  Número de quartos: #{@num_rooms}\n"
        str += "  Para: #{@num_people} pessoas\n"
        str += "  Data de entrada: #{@entrance_date.strftime('%d/%m/%Y')}\n"
        str += "  Data de saida: #{@exit_date.strftime('%d/%m/%Y')}\n"
        str += "  Preço: #{@price}\n"
        str += "-----------------------------------\n"

        return str
    end

end